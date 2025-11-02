# Script para detener Jenkins
# Uso: .\scripts\stop-jenkins.ps1

Write-Host "🛑 Deteniendo Jenkins..." -ForegroundColor Yellow

$container = docker ps -a --filter "name=jenkins" --format "{{.Names}}"
if ($container) {
    docker stop jenkins
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Jenkins detenido" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Jenkins ya estaba detenido" -ForegroundColor Yellow
    }
    
    $remove = Read-Host "¿Eliminar el contenedor? (S/N)"
    if ($remove -eq "S" -or $remove -eq "s") {
        docker rm jenkins
        Write-Host "✅ Contenedor eliminado" -ForegroundColor Green
        Write-Host ""
        Write-Host "💡 Nota: Los datos están guardados en el volumen 'jenkins_home'" -ForegroundColor Cyan
        Write-Host "   Para eliminar también los datos: docker volume rm jenkins_home" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ No se encontró contenedor Jenkins" -ForegroundColor Red
}

