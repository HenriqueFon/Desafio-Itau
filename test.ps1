$ProjectPath = (Get-Location).Path

Write-Host "Rodando testes automatizados dentro de um container Maven..."

docker run --rm `
  -v "${ProjectPath}:/app" `
  -w /app `
  maven:3.9.6-eclipse-temurin-17 `
  mvn test