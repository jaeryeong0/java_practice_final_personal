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
Write-Host "[1/2] Compiling..." -ForegroundColor Yellow

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

# Launch single test window
Write-Host ""
Write-Host "[2/2] Launching test window ($n players)..." -ForegroundColor Yellow

Start-Process $javaw -ArgumentList @("-cp", $cp, "TestMain", $n)

Write-Host ""
Write-Host "[DONE] Test window launched." -ForegroundColor Green
Write-Host ""
Write-Host "  In-window controls:" -ForegroundColor DarkCyan
Write-Host "    [  = switch to previous player view" -ForegroundColor DarkCyan
Write-Host "    ]  = switch to next player view" -ForegroundColor DarkCyan
Write-Host "    Input goes to the currently shown player." -ForegroundColor DarkCyan
Write-Host ""
Write-Host "  Typical flow:" -ForegroundColor DarkCyan
Write-Host "    P1 (HOST)  : enter nickname -> Enter port to start server" -ForegroundColor DarkCyan
Write-Host "    P2..PN (CLIENT): enter nickname -> enter localhost:<port> to join" -ForegroundColor DarkCyan
Write-Host ""
