# Script avanzado para limpiar caracteres corruptos de emojis
$readmePath = 'README.md'

# Asegurar que se lea el archivo en UTF-8
$content = Get-Content $readmePath -Encoding UTF8 -Raw

# Guardar backup
Copy-Item $readmePath '$readmePath.backup.txt'

# Remover caracteres corruptos de emojis más comunes
$cleanPatterns = @{
    'ðŸ"' = ''
    'ðŸŽ' = ''
    'ðŸš' = ''
    'ðŸ"Š' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ' = ''
    'ðŸª' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'ðŸ"' = ''
    'â'' = ''
    'âœ' = ''
    'â' = ''
    'â' = 'ℹ'
    'â' = ''
    'âš' = ''
    'â' = ''
}

foreach ($pattern in $cleanPatterns.Keys) {
    $replacement = $cleanPatterns[$pattern]
    if ($content -match [regex]::Escape($pattern)) {
        Write-Host "Reemplazando $pattern con $replacement"
        $content = $content -replace [regex]::Escape($pattern), $replacement
    }
}

# Remover cualquier carácter de emoji completo restante (patrón general)
$content = $content -replace '[\x{1F600}-\x{1F64F}]', ''  # Emoticons
$content = $content -replace '[\x{1F300}-\x{1F5FF}]', ''  # Símbolos varios
$content = $content -replace '[\x{1F680}-\x{1F6FF}]', ''  # Transporte y mapas
$content = $content -replace '[\x{1F1E0}-\x{1F1FF}]', ''  # Banderas

# Guardar el archivo limpio
$content | Set-Content $readmePath -Encoding UTF8

Write-Host 'Archivo README.md limpiado completamente'
