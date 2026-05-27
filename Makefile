SHELL := /bin/bash
EDGE_NET := edge

# Usage: make up s=proxy   |   make logs s=proxy
COMPOSE = docker compose -f stacks/$(s)/compose.yaml

.PHONY: help network up down restart logs ps pull backup

help: ## List available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

network: ## Create the shared edge network (run once)
	@docker network inspect $(EDGE_NET) >/dev/null 2>&1 || docker network create $(EDGE_NET)

up: network ## Start a stack:  make up s=proxy
	$(COMPOSE) up -d

down: ## Stop a stack:  make down s=proxy
	$(COMPOSE) down

restart: ## Restart a stack:  make restart s=proxy
	$(COMPOSE) restart

logs: ## Tail logs:  make logs s=proxy
	$(COMPOSE) logs -f --tail=100

ps: ## List a stack's containers:  make ps s=proxy
	$(COMPOSE) ps

pull: ## Pull latest images:  make pull s=proxy
	$(COMPOSE) pull

backup: ## Backup a stack's data:  make backup s=proxy
	@bash backup/$(s)-backup.sh
