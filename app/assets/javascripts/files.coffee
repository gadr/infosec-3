window.privateKey = ""
window.encodedPrivateKey = ""

privateChangeHandler = ->
	console.log "File", this.files[0]
	reader = new FileReader()
	reader.onload = (e) ->
		window.encodedPrivateKey = new Uint8Array( e.target.result )		
		console.log("Read:", window.encodedPrivateKey)
	reader.onerror = (error) ->
		console.log error
	reader.readAsArrayBuffer(this.files[0])
	return true

getPublicKey = ->
	xhr = new XMLHttpRequest();
	xhr.open('GET', '/getPublic', true);
	xhr.responseType = 'arraybuffer';
	xhr.onload = (e) ->
		window.publicKey = new Uint8Array(this.response)
		console.log publicKey		
	xhr

filesChangeHandler = () ->	
	@filesArray = this.files
	@filesLoaded = 0
	for file, i in @filesArray
		reader = new FileReader()		
		reader.onload = do =>			
			name = file.name
			return (e) =>
				content = new Uint8Array( e.target.result )				
				for files in @filesArray
					if (files.name is name)
						files.content = content
						files.simpleName = files.name.split(".")[0]
						@filesLoaded++
						if @filesLoaded is @filesArray.length
							createListItem(@filesArray)
						break
		reader.onerror = (error) =>
			console.error error
		reader.readAsArrayBuffer(file)

	window.filesArray = @filesArray
	return true

createListItem = (files) ->
	indexFiles = []
	for indexFile in files		
		if indexFile.name.split(".")[0] is "index"
			indexFiles.push(indexFile)
	
	if indexFiles.length isnt 3
		console.error "Insira todos os arquivos index"
		return

	indexEnv = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "env")
	indexAsd = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "asd")
	indexEnc = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "enc")
	
	indexResult = window.InfosecApplet.getIndex(window.privateKey, window.publicKey, indexEnv.content, indexAsd.content, indexEnc.content)	
	indexContent = _.reject(indexResult.split("\n"), (i) -> return i.length is 0 )
	
	window.map = {}
	for fileLine in indexContent
		fileInfo = fileLine.split(" ")
		window.map[fileInfo[1]] = fileInfo[0]

	alreadyAdded = {}
	for allowedFile, a in files
		continue if not map[allowedFile.simpleName] or alreadyAdded[allowedFile.simpleName]		
		relatedFiles = []
		_.each files, (f) ->
			relatedFiles.push(f) if f.simpleName is allowedFile.simpleName	
		continue if relatedFiles.length isnt 3

		fileEnv = _.find(relatedFiles, (i) -> return i if i.name.split(".")[1] is "env")
		fileAsd = _.find(relatedFiles, (i) -> return i if i.name.split(".")[1] is "asd")		
		fileEnc = _.find(relatedFiles, (i) -> return i if i.name.split(".")[1] is "enc")
		fileResult = window.InfosecApplet.checkStatus(window.privateKey, window.publicKey, fileEnv.content, fileAsd.content, fileEnc.content)
		downloadLink = ""
		if fileResult is "OK"
			fileBase64 = window.InfosecApplet.getBase64File(window.privateKey, window.publicKey, fileEnv.content, fileAsd.content, fileEnc.content)
			downloadLink = "<a download="+map[allowedFile.simpleName]+" href=\"data:application/octet-stream;charset=utf-8;base64,"+fileBase64+"\">"+map[allowedFile.simpleName]+"</a>"
		if downloadLink is "" then downloadLink = map[allowedFile.simpleName]
		$tr = $("<tr><td>"+downloadLink+"</td><td>"+allowedFile.simpleName+"</td><td>"+fileResult+"</td></tr>")		
		alreadyAdded[allowedFile.simpleName] = true
		$("#files-table tbody").append($tr)
		$("#files-table").fadeIn()


$(document).ready -> getPublicKey().send()
$("#private").submit (e) ->
	e.preventDefault()
	password = $("#password").val()
	window.privateKey = new Uint8Array(window.InfosecApplet.decryptPrivateKey(window.encodedPrivateKey, password))
$('#privateKeyPath').change privateChangeHandler
$('#files').change filesChangeHandler