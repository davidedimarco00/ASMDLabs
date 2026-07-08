$ErrorActionPreference = "Stop"

$services = @(
    "parking-service",
    "ticketing-service",
    "payment-service",
    "analytics-service",
    "auth-service", 
    "embedded-service"
)

Write-Host ">>> Starting Gradle tests for all microservices"

foreach ($svc in $services) {
    Write-Host ""
    Write-Host "-------------------------------------------"
    Write-Host ">>> Testing $svc"
    Write-Host "-------------------------------------------"

    Set-Location $svc
    gradle test
    Set-Location ..
}

Write-Host ""
Write-Host ">>> ALL TESTS COMPLETED SUCCESSFULLY"