$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

Write-Host "===========================" -ForegroundColor Cyan
Write-Host "   BANG!   Test Launcher   " -ForegroundColor Cyan
Write-Host "===========================" -ForegroundColor Cyan
Write-Host ""

# Number of players
do {
    $raw = Read-Host "Number of players (4-7)"
    $n   = $raw -as [int]
} while (-not $n -or $n -lt 4 -or $n -gt 7)

# Player names
$names = @()
for ($i = 0; $i -lt $n; $i++) {
    $default = "P$($i + 1)"
    $name    = Read-Host "  Player $($i + 1) name  [Enter = $default]"
    if ([string]::IsNullOrWhiteSpace($name)) { $name = $default }
    $names  += $name
}

# Find Java 11
$java11bin = $null
$searchRoots = @(
    "C:\Program Files\Java",
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Microsoft",
    "C:\Program Files\BellSoft",
    "C:\Program Files\Amazon Corretto",
    "C:\Program Files\Zulu"
)
foreach ($searchRoot in $searchRoots) {
    if (Test-Path $searchRoot) {
        $dir = Get-ChildItem $searchRoot -Directory |
               Where-Object { $_.Name -match '(jdk|jre)[^\d]*11' } |
               Select-Object -First 1
        if ($dir) { $java11bin = Join-Path $dir.FullName "bin"; break }
    }
}
if ($java11bin) {
    $javac = Join-Path $java11bin "javac.exe"
    $javaw = Join-Path $java11bin "javaw.exe"
    Write-Host "[OK]  Java 11: $java11bin" -ForegroundColor Green
} else {
    Write-Host "[WARN] Java 11 not found in common paths; using default java." -ForegroundColor Yellow
    $javac = "javac"
    $javaw = "javaw"
}

# Paths
$lib = Join-Path $root "project\lib\lanterna-3.1.2.jar"
$bin = Join-Path $root "project\bin"
$cp  = "$bin;$lib"

# Compile
Write-Host ""
Write-Host "[1/3] Compiling..." -ForegroundColor Yellow

if (-not (Test-Path $bin)) { New-Item -ItemType Directory $bin | Out-Null }

$sources = Get-ChildItem -Recurse (Join-Path $root "project") -Filter "*.java" |
           Where-Object { $_.FullName -notlike "*\bin\*" } |
           Select-Object -ExpandProperty FullName

$javaArgs = @("-encoding", "UTF-8", "-cp", $lib, "-d", $bin) + $sources
& $javac $javaArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "[FAIL] Compilation failed. Fix errors and try again." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "[OK]   Compiled successfully." -ForegroundColor Green

# Launch
Write-Host ""
Write-Host "[2/3] Launching..." -ForegroundColor Yellow

$launchArgs = @("-cp", $cp, "TestMain") + $names
Start-Process $javaw -ArgumentList $launchArgs

Write-Host ""
Write-Host "[3/3] Done.  Single window launched." -ForegroundColor Green
Write-Host "  Use  [  and  ]  to switch between player views." -ForegroundColor DarkCyan
Write-Host ""
