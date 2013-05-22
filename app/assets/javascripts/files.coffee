window.privateKey = ""

changeHandler = () ->	
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

$('#files').change changeHandler