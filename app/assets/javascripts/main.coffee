class SecKeyboard
	constructor: ->
		console.log 'Hey!'

window.secKeyboard = new SecKeyboard()

window.privateKey = ""

createPostSignatureXHR = ->
	xhr = new XMLHttpRequest();
	xhr.open('POST', '/signature', true);
	xhr.onload = ->
		if this.response is "OK" and this.status is 200
			console.log 'success'
			window.location.href = "/"
		else
			console.error 'unauthorized!'
			$(".alert-error.signature").fadeIn()
	xhr

createRandomXHR = ->
	xhr = new XMLHttpRequest();
	xhr.open('GET', '/random', true);
	xhr.responseType = 'arraybuffer';
	xhr.onload = (e) ->
		randomBytes = new Uint8Array(this.response)
		console.log randomBytes
		password = $('#password').val()
		signature = new Uint8Array(document.InfosecApplet.sign(password, window.privateKey, randomBytes))
		console.log "Signed:", signature
		createPostSignatureXHR().send(signature);
	xhr

checkDigitalSignature = ->
	createRandomXHR().send()
	return false

changeHandler = (file) ->
	console.log "File", this.files[0]
	reader = new FileReader()
	reader.onload = (e) ->
		window.privateKey = new Uint8Array( e.target.result )
		console.log("Read:", window.privateKey)
	reader.onerror = (error) ->
		console.log error
	reader.readAsArrayBuffer(this.files[0])
	return true

usernameFormHandler = ->
	username = $('#username').val()
	usernamePromise = $.post('/login', username: username)
	usernamePromise.done (data) ->
		console.log "Exists!"
		$(".alert-success.username").fadeIn()
		$(".alert-error.username").fadeOut()
	usernamePromise.fail (jqXHR) ->
		console.log jqXHR.status
		$(".alert-success.username").fadeOut()
		$(".alert-error.username").fadeIn()

	return false

passwordFormHandler = ->
	password = $('#password').val()
	return false

$('#loginForm').submit usernameFormHandler
$('#passwordForm').submit passwordFormHandler
$('#checkSignature').submit checkDigitalSignature
$('#file').change changeHandler