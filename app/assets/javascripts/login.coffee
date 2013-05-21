class SecKeyboard
	constructor: ->
		console.log 'Hey!'
		@phonemes = ["BA", "CA", "DA", "FA", "GA", "JA", "BE", "BO", "CE", "CO", "DE", "DO", "FE", "FO", "GE", "GO", "JE", "JO", "MA", "ME", "RA", "RO", "VE", "VO"]
		@buttonMap =
			button0: []
			button1: []
			button2: []
			button3: []
			button4: []
			button5: []

	generateRandomButtons: ->
		phonemesCopy = @phonemes[..]
		total = 24
		# For each button...
		for i in [0..5]
			# Generate 4 phonemes
			for j in [0..3]
				random = Math.random() * total
				total = total - 1


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

changeHandler = ->
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