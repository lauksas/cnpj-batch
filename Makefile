# definition of the default target
.DEFAULT_GOAL = image

# shell target is phony (no files to check)
.PHONY = shell

NAME = cnpj-batch

REGISTRY_URL = 
IMAGE_REPO = $(NAME)
IMAGE_TAG = latest
SECRET_NAME = $(NAME)-secret
NAMESPACE = mux
PODMAN_TLS = false
DEPLOYMENT_NAME = $(NAME)
CHART_PATH = ./helm/$(NAME)
ENV = local
FLYWAY_CONFIG = flyway-$(ENV).conf

IMAGE_URL = $(IMAGE_REPO):$(IMAGE_TAG)

ifneq ($(REGISTRY_URL),)
	IMAGE_URL = $(REGISTRY_URL)/$(IMAGE_REPO):$(IMAGE_TAG)
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

secret:
	read -p "It will create the secret based on secrets.env, check the file and press any key to continue" && kubectl create secret generic $(SECRET_NAME) --save-config --dry-run=client --from-env-file=secrets.env -n $(NAMESPACE) -o yaml | kubectl apply -f -

pvc:
	kubectl apply -n $(NAMESPACE) -f helm/pvc.yaml

install: pvc
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values.yaml

dry-run:
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values.yaml --dry-run --debug

lint:
	helm lint $(CHART_PATH)

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
