SHELL := /bin/bash
EDGE_NET := edge

# Shared Compose command for stack targets.
COMPOSE = docker compose --env-file stacks/$(s)/.env -f stacks/$(s)/compose.yaml

# Terraform command scoped by environment.
TERRAFORM = terraform -chdir=terraform/envs/$(e)

# Required arguments for stack and Terraform targets.
require_s = @if [ -z "$(s)" ]; then echo "Usage: make $@ s=<stack>"; exit 2; fi
require_e = @if [ -z "$(e)" ]; then echo "Usage: make $@ e=<env>"; exit 2; fi

.PHONY: help network up down restart logs ps pull backup lint fmt \
        tf-init tf-plan tf-apply tf-destroy

help: ## List available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

network: ## Create the shared edge network (run once)
	@docker network inspect $(EDGE_NET) >/dev/null 2>&1 || docker network create $(EDGE_NET)

up: ## Start a stack:  make up s=proxy
	$(require_s)
	@$(MAKE) --no-print-directory network
	$(COMPOSE) up -d

down: ## Stop a stack:  make down s=proxy
	$(require_s)
	$(COMPOSE) down

restart: ## Restart a stack:  make restart s=proxy
	$(require_s)
	$(COMPOSE) restart

logs: ## Tail logs:  make logs s=proxy
	$(require_s)
	$(COMPOSE) logs -f --tail=100

ps: ## List a stack's containers:  make ps s=proxy
	$(require_s)
	$(COMPOSE) ps

pull: ## Pull configured images:  make pull s=proxy
	$(require_s)
	$(COMPOSE) pull

backup: ## Backup a stack's data:  make backup s=proxy
	$(require_s)
	@bash backup/$(s)-backup.sh

lint: ## Run shellcheck, terraform fmt -check, and compose config validation
	@shellcheck -x -S warning backup/*.sh backup/lib/*.sh
	@terraform fmt -check -recursive terraform/
	@set -e; \
	temp_envs=(); \
	cleanup() { if [ "$${#temp_envs[@]}" -gt 0 ]; then rm -f "$${temp_envs[@]}"; fi; }; \
	trap cleanup EXIT; \
	for f in stacks/*/compose.yaml; do \
		dir="$$(dirname $$f)"; \
		env_file="$$dir/.env"; \
		if [ -f "$$dir/.env.example" ] && [ ! -f "$$env_file" ]; then \
			cp "$$dir/.env.example" "$$env_file"; \
			temp_envs+=("$$env_file"); \
		fi; \
		compose_args=(); \
		if [ -f "$$env_file" ]; then compose_args=(--env-file "$$env_file"); fi; \
		docker compose "$${compose_args[@]}" -f "$$f" config --quiet; \
	done
	@echo "lint passed"

fmt: ## Apply terraform fmt to all .tf files
	@terraform fmt -recursive terraform/

tf-init: ## Init Terraform backend:  make tf-init e=prod
	$(require_e)
	$(TERRAFORM) init -backend-config=backend.hcl

tf-plan: ## Preview Terraform changes:  make tf-plan e=prod
	$(require_e)
	$(TERRAFORM) plan

tf-apply: ## Apply Terraform changes:  make tf-apply e=prod
	$(require_e)
	$(TERRAFORM) apply

tf-destroy: ## Destroy Terraform-managed resources:  make tf-destroy e=prod
	$(require_e)
	$(TERRAFORM) destroy
