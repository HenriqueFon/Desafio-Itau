$ImageName = "desafio-itau-backend-api"
$ContainerName = "desafio-itau-backend-container"
$Port = 8080

Write-Host "Criando imagem Docker..."
docker build -t $ImageName .

Write-Host "Parando container antigo, se existir..."
docker stop $ContainerName 2>$null

Write-Host "Removendo container antigo, se existir..."
docker rm $ContainerName 2>$null

Write-Host "Subindo aplicacao..."
docker run -d `
  --name $ContainerName `
  -p ${Port}:8080 `
  $ImageName

Write-Host ""
Write-Host "Aplicacao rodando em: http://localhost:$Port/swagger-ui/index.html"
Write-Host "Para ver os logs:"
Write-Host "docker logs -f $ContainerName"