$STATIC_FOLDER = "..\src\main\resources\static"

npm run build
$index = Get-Content .\build\index.html
$index = $index.Replace("/static/js", "/js").Replace("/static/css", "/css")
Out-File -FilePath .\build\index.html -Encoding utf8 -InputObject $index

$cssFile = Get-ChildItem .\build\static\css -Filter *.css
$cssPath = Join-Path -Path $cssFile.DirectoryName -ChildPath $cssFile.ToString()
$cssContent = Get-Content $cssPath
$cssContent = $cssContent.Replace("/static/media", "/media")
Out-File -FilePath $cssPath -Encoding utf8 -InputObject $cssContent

Remove-Item $STATIC_FOLDER\* -Recurse
Copy-Item -Path .\build\index.html -Destination $STATIC_FOLDER
Copy-Item -Path .\build\asset-manifest.json -Destination $STATIC_FOLDER
Copy-Item -Path .\build\static\js -Destination $STATIC_FOLDER -Recurse
Copy-Item -Path .\build\static\css -Destination $STATIC_FOLDER -Recurse
Copy-Item -Path .\build\static\media -Destination $STATIC_FOLDER -Recurse