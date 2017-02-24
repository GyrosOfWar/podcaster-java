$STATIC_FOLDER = "..\src\main\resources\static"

npm run build
$index = Get-Content .\build\index.html
$index = $index.Replace("/static/js", "/js").Replace("/static/css", "/css");
Out-File -FilePath .\build\index.html -Encoding utf8 -InputObject $index

Remove-Item $STATIC_FOLDER\* -Recurse
Copy-Item -Path .\build\index.html -Destination $STATIC_FOLDER
Copy-Item -Path .\build\asset-manifest.json -Destination $STATIC_FOLDER
Copy-Item -Path .\build\static\js -Destination $STATIC_FOLDER -Recurse
Copy-Item -Path .\build\static\css -Destination $STATIC_FOLDER -Recurse