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

filesChangeHandler = () ->	
	@filesArray = this.files
	for file, i in @filesArray
		reader = new FileReader()
		reader.onload = do =>
			name = file.name
			return (e) =>
				content = new Uint8Array( e.target.result )				
				for files in @filesArray
					if (files.name is name)
						files.content = content
						break
		reader.onerror = (error) =>
			console.error error
		reader.readAsArrayBuffer(file)

	window.filesArray = @filesArray
	createListItem(filesArray)
	return true

createListItem = (files) ->
	for file in files		
		$li = $("<li><a href='javacript:void(0);'>"+file.name+"</a></li>").data('file', file)
		$li.click(-> console.log $(this).data('file'))
		$("#files-list").append($li)


$("#private").submit (e) ->
	e.preventDefault()
	password = $("#password").val()
	window.privateKey = new Uint8Array(window.InfosecApplet.decryptPrivateKey(window.encodedPrivateKey, password))
$('#privateKeyPath').change privateChangeHandler
$('#files').change filesChangeHandler