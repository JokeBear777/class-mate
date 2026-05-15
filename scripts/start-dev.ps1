$ErrorActionPreference = "Stop"

$backendRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$frontendRoot = Resolve-Path (Join-Path $backendRoot "..\class-mate-frontend")
$logDir = Join-Path $backendRoot "build\dev-logs"

New-Item -ItemType Directory -Force -Path $logDir | Out-Null

function Test-PortListening {
    param([int] $Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    return $null -ne $connection
}

function Start-HiddenRunner {
    param(
        [string] $RunnerPath,
        [string] $WorkingDirectory
    )

    $processInfo = New-Object System.Diagnostics.ProcessStartInfo
    $processInfo.FileName = $RunnerPath
    $processInfo.WorkingDirectory = $WorkingDirectory
    $processInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
    $processInfo.CreateNoWindow = $true
    $processInfo.UseShellExecute = $true

    return [System.Diagnostics.Process]::Start($processInfo)
}

Write-Output "[class-mate] Starting Docker services..."
docker compose --project-directory $backendRoot up -d

$backendLog = Join-Path $logDir "backend.log"
$backendErr = Join-Path $logDir "backend.err.log"
$frontendLog = Join-Path $logDir "frontend.log"
$frontendErr = Join-Path $logDir "frontend.err.log"

if (Test-PortListening -Port 8080) {
    Write-Output "[class-mate] Backend already appears to be listening on http://localhost:8080"
} else {
    Write-Output "[class-mate] Starting backend..."
    $backendRunner = Join-Path $logDir "run-backend.cmd"
    @(
        "@echo off",
        "cd /d ""$backendRoot""",
        "call ""$(Join-Path $backendRoot "gradlew.bat")"" bootRun 1> ""$backendLog"" 2> ""$backendErr"""
    ) | Set-Content -Path $backendRunner -Encoding Default
    $backendProcess = Start-HiddenRunner -RunnerPath $backendRunner -WorkingDirectory $backendRoot
    $backendProcess.Id | Set-Content (Join-Path $logDir "backend.pid")
    Write-Output "[class-mate] Backend PID: $($backendProcess.Id)"
}

if (Test-PortListening -Port 5173) {
    Write-Output "[class-mate] Frontend already appears to be listening on http://localhost:5173"
} else {
    Write-Output "[class-mate] Starting frontend..."
    $npm = (Get-Command npm.cmd -ErrorAction Stop).Source
    $frontendRunner = Join-Path $logDir "run-frontend.cmd"
    @(
        "@echo off",
        "cd /d ""$frontendRoot""",
        "call ""$npm"" run dev -- --host 127.0.0.1 1> ""$frontendLog"" 2> ""$frontendErr"""
    ) | Set-Content -Path $frontendRunner -Encoding Default
    $frontendProcess = Start-HiddenRunner -RunnerPath $frontendRunner -WorkingDirectory $frontendRoot
    $frontendProcess.Id | Set-Content (Join-Path $logDir "frontend.pid")
    Write-Output "[class-mate] Frontend PID: $($frontendProcess.Id)"
}

Write-Output ""
Write-Output "[class-mate] URLs"
Write-Output "  Frontend: http://localhost:5173"
Write-Output "  Backend:  http://localhost:8080"
Write-Output "  Swagger:  http://localhost:8080/swagger-ui/index.html"
Write-Output ""
Write-Output "[class-mate] Logs"
Write-Output "  Backend:  $backendLog"
Write-Output "  Frontend: $frontendLog"
