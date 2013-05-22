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
	for file in files		
		if file.name.split(".")[0] is "index"
			indexFiles.push(file)
	
	if indexFiles.length isnt 3
		console.error "Insira todos os arquivos index"
		return

	indexEnc = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "enc")
	indexEnv = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "env")
	indexAsd = _.find(indexFiles, (i) -> return i if i.name.split(".")[1] is "asd")
	
	window.indexContent = window.InfosecApplet.getIndex(window.privateKey, window.publicKey, indexEnv.content, indexAsd.content, indexEnc.content)	
	indexContent = _.reject(indexContent.split("\n"), (i) -> return i.length is 0 )
	
	for file in indexContent
		fileInfo = file.split(" ")
		map[fileInfo[1]] = fileInfo[0]

	window.map = map
	###$li = $("<li><a href='javacript:void(0);'>"+file.name+"</a></li>").data('file', file)
	$li.click ->
		fileInfo = $(this).data('file')
		simpleName = fileInfo.name.split(".")[0]
		relatedFiles = []
		$("#files-list li").each (i,l) ->
			lInfo = $(l).data('file')
			if (lInfo.name.split(".")[0] is simpleName)
				relatedFiles.push(lInfo)
		console.log relatedFiles
	$("#files-list").append($li)###


$(document).ready -> getPublicKey().send()
$("#private").submit (e) ->
	e.preventDefault()
	password = $("#password").val()
	window.privateKey = new Uint8Array(window.InfosecApplet.decryptPrivateKey(window.encodedPrivateKey, password))
$('#privateKeyPath').change privateChangeHandler
$('#files').change filesChangeHandler