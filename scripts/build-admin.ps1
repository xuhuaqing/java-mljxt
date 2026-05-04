Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$adminWeb = Join-Path $projectRoot "admin-web"
$targetDir = Join-Path $projectRoot "src\\main\\resources\\static\\admin"

Push-Location $adminWeb
npm install
npm run build
Pop-Location

if (Test-Path $targetDir) {
    Remove-Item -Recurse -Force $targetDir
}
New-Item -ItemType Directory -Path $targetDir | Out-Null
Copy-Item -Recurse -Force (Join-Path $adminWeb "dist\\*") $targetDir

Write-Host "Admin frontend copied to $targetDir"
