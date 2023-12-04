# definition of the default target
.DEFAULT_GOAL = image

# shell target is phony (no files to check)
.PHONY = shell

NAME = cnpj-batch

REGISTRY_URL = registry.cube.local:5000
IMAGE_REPO = $(NAME)
IMAGE_TAG = latest
SECRET_NAME = $(NAME)-secret
NAMESPACE = mux
PODMAN_TLS = false
DEPLOYMENT_NAME = $(NAME)
CHART_PATH = ./helm/$(NAME)
ENV = dev
FLYWAY_CONFIG = flyway-$(ENV).conf
DB_HOST = 192.168.1.250
DB_USER = cnpj_batch_readwrite

IMAGE_URL = $(IMAGE_REPO):$(IMAGE_TAG)

ifneq ($(REGISTRY_URL),)
	IMAGE_URL = $(REGISTRY_URL)/$(IMAGE_REPO):$(IMAGE_TAG)
endif

ifeq ($(ENV),prod)
	DB_HOST = grupomux.com.br
endif

namespace:
	kubectl create namespace $(NAMESPACE) --dry-run=client -o yaml | kubectl apply -f -

image:
	podman build -t $(IMAGE_URL) .

shell: image
	podman run -it --entrypoint "/bin/bash" $(IMAGE_URL)

run: image
	podman run --env-file=.env --rm -v ./data:/data $(IMAGE_URL)

push: image
	podman push --tls-verify=$(PODMAN_TLS) $(IMAGE_URL)

image-prune:
	podman rmi $(IMAGE_URL); skopeo delete --tls-verify=$(PODMAN_TLS) docker://$(IMAGE_URL)

reset-db-password:
	echo "changing $(DB_USER) password"; \
	echo; \
	read -s -p "Enter current postgres (admin) password:" PGPASSWORD; \
	echo; \
	read -s -p "Enter new $(DB_USER) password:" NEW_PASSWORD; \
	echo; \
	echo; \
	export PGPASSWORD=$$PGPASSWORD; \
	psql -h $(DB_HOST) -p 5432 -U postgres -d postgres -c "ALTER USER $(DB_USER) WITH PASSWORD '$$NEW_PASSWORD';";

secret: reset-db-password
	read -s -p "Enter db password for $(DB_USER):" cnpj_batch_readwrite_password; echo; \
	kubectl create secret -n $(NAMESPACE) generic $(SECRET_NAME) \
		--from-literal=CNPJ_BATCH_DATASOURCE_USERNAME=$(DB_USER) \
		--from-literal=CNPJ_BATCH_DATASOURCE_PASSWORD=$$cnpj_batch_readwrite_password \
		--dry-run=client -o yaml | kubectl apply -f -

pvc:
	kubectl apply -n $(NAMESPACE) -f helm/pvc.yaml

install:
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values-$(ENV).yaml

dry-run:
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values-$(ENV).yaml --dry-run --debug

lint:
	helm lint $(CHART_PATH) --values=$(CHART_PATH)/values-$(ENV).yaml

delete:
	helm delete -n $(NAMESPACE) $(DEPLOYMENT_NAME)

run-job: install delete-job
	kubectl create job -n $(NAMESPACE) --from=cronjob/$(DEPLOYMENT_NAME) $(NAME)-import

delete-job:
	kubectl -n $(NAMESPACE) delete jobs.batch $(NAME)-import || true

flyway-info:
	cat $(FLYWAY_CONFIG); echo; \
	read -p "check the $(FLYWAY_CONFIG) file contents and press any key to continue" && \
	read -p "Enter db username:" db_username; echo; \
	read -s -p "Enter db password:" db_password; echo; \
	mvn flyway:info -Dflyway.user=$$db_username -Dflyway.password=$$db_password -Dflyway.configFiles=$(FLYWAY_CONFIG)

flyway-migrate:
	cat $(FLYWAY_CONFIG); echo; \
	read -p "check the $(FLYWAY_CONFIG) file contents and press any key to continue" && \
	read -p "Enter db username:" db_username; \
	read -s -p "Enter db password:" db_password; \
	mvn flyway:migrate -Dflyway.user=$$db_username -Dflyway.password=$$db_password -Dflyway.configFiles=$(FLYWAY_CONFIG)

prune-all: image-prune delete-job delete

install-all: namespace secret pvc push install
