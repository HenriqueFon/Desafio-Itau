APP_NAME=desafio-itau-backend
IMAGE_NAME=desafio-itau-backend-api
CONTAINER_NAME=desafio-itau-backend-container
PORT=8080

.PHONY: help build run stop restart logs clean test-api test-api-now

help:
	@echo "Comandos disponíveis:"
	@echo "  make build        - Cria a imagem Docker da aplicação"
	@echo "  make run          - Sobe a aplicação em container"
	@echo "  make stop         - Para e remove o container"
	@echo "  make restart      - Reinicia o container"
	@echo "  make logs         - Mostra os logs do container"
	@echo "  make clean        - Remove container e imagem"
	@echo "  make test-api     - Testa os endpoints com uma data fixa"
	@echo "  make test-api-now - Testa os endpoints com data atual"

build:
	docker build -t $(IMAGE_NAME) .

run: build
	-docker stop $(CONTAINER_NAME)
	-docker rm $(CONTAINER_NAME)
	docker run -d \
		--name $(CONTAINER_NAME) \
		-p $(PORT):8080 \
		$(IMAGE_NAME)

stop:
	-docker stop $(CONTAINER_NAME)
	-docker rm $(CONTAINER_NAME)

restart: stop run

logs:
	docker logs -f $(CONTAINER_NAME)

clean: stop
	-docker rmi $(IMAGE_NAME)

test-api:
	@echo "Limpando transações..."
	curl -i -X DELETE http://localhost:$(PORT)/transacao
	@echo ""
	@echo "Criando transação com data fixa..."
	curl -i -X POST http://localhost:$(PORT)/transacao \
		-H "Content-Type: application/json" \
		-d '{"valor":123.45,"dataHora":"2020-08-07T12:34:56.789-03:00"}'
	@echo ""
	@echo "Buscando estatísticas..."
	curl -i http://localhost:$(PORT)/estatistica

test-api-now:
	@echo "Limpando transações..."
	curl -i -X DELETE http://localhost:$(PORT)/transacao
	@echo ""
	@echo "Criando transação com data atual..."
	curl -i -X POST http://localhost:$(PORT)/transacao \
		-H "Content-Type: application/json" \
		-d "{\"valor\":123.45,\"dataHora\":\"$$(date -Iseconds)\"}"
	@echo ""
	@echo "Buscando estatísticas..."
	curl -i http://localhost:$(PORT)/estatistica