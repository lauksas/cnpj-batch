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

IMAGE_URL = $(IMAGE_REPO):$(IMAGE_TAG)

ifneq ($(REGISTRY_URL),)
	IMAGE_URL = $(REGISTRY_URL)/$(IMAGE_REPO):$(IMAGE_TAG)
endif

image:
	podman build -t $(IMAGE_URL) .

shell: image
	podman run -it --entrypoint "/bin/bash" $(IMAGE_URL)

run: image
	podman run --env-file=.env --rm -v ./data:/data $(IMAGE_URL)

push: image
	podman push --tls-verify=$(PODMAN_TLS) $(IMAGE_URL)

secret:
	read -p "It will create the secret based on secrets.env, check the file and press any key to continue" && kubectl create secret generic $(SECRET_NAME) --from-env-file=secrets.env -n $(NAMESPACE)

pvc:
	kubectl apply -n $(NAMESPACE) -f helm/pvc.yaml

install: pvc
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values.yaml

dry-run:
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values.yaml --dry-run --debug

lint:
	helm lint $(CH)

delete:
	helm delete -n $(NAMESPACE) $(DEPLOYMENT_NAME)

run-job: install delete-job
	kubectl create job -n $(NAMESPACE) --from=cronjob/$(DEPLOYMENT_NAME) $(NAME)-import

delete-job:
	kubectl -n $(NAMESPACE) delete jobs.batch $(NAME)-import || true