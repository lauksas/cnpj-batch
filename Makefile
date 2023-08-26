# definition of the default target
.DEFAULT_GOAL = image

# shell target is phony (no files to check)
.PHONY = shell

NAME = cnpj-batch

REGISTRY_URL = 
IMAGE_NAME = $(NAME)
IMAGE_TAG = latest
SECRET_NAME = $(NAME)-secret
NAMESPACE = mux
PODMAN_TLS = false
DEPLOYMENT_NAME = $(NAME)
CHART_PATH = ./helm/$(NAME)

IMAGE_URL = $(IMAGE_NAME):$(IMAGE_TAG)

ifneq ($(REGISTRY_URL),)
	IMAGE_URL = $(REGISTRY_URL)/$(IMAGE_NAME):$(IMAGE_TAG)
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
	read -p "It will create the secret, press any key to continue" && kubectl create secret generic $(SECRET_NAME) --from-env-file=secrets.env -n $(NAMESPACE)

install: 
	helm upgrade --install --create-namespace -n $(NAMESPACE) $(DEPLOYMENT_NAME) $(CHART_PATH) --values=$(CHART_PATH)/values.yaml