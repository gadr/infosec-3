class SecKeyboard
	constructor: ->
		console.log 'Hey!'
		@phonemes = ["BA", "CA", "DA", "FA", "GA", "JA", "BE", "BO", "CE", "CO", "DE", "DO", "FE", "FO", "GE", "GO", "JE", "JO", "MA", "ME", "RA", "RO", "VE", "VO"]
		@chosenPhonemes = []
		@createButtons()
		$('#clear-password').click @clearPassword

	clearPassword: (e) =>
		$('#password').val('')
		@chosenPhonemes = []

	clickButton: (e) =>
		return false if @chosenPhonemes.length is 3
		separator = if @chosenPhonemes.length is 0 then '' else ' '
		val = $(e.target).val()
		@chosenPhonemes.push val
		$('#password').val($('#password').val() + separator + val)
		@createButtons()

	createButtons: =>
		@buttonMap = @generateRandomButtons(@phonemes)
		buttons = []
		for i in [0..5]
			phonemes = @buttonMap['button'+i].join('-')
			buttonDOM = $("<input type='button' value='#{phonemes}' id='button#{i}'/>")
			buttonDOM.click @clickButton
			buttons.push buttonDOM
		$("#sec-keyboard").html(buttons)

	generateRandomButtons: (phonemes) ->
		phonemesCopy = phonemes.slice(0)
		total = 24
		buttonMap = {}
		# For each button...
		for i in [0..5]
			buttonMap['button'+i] = []
			# Generate 4 phonemes
			for j in [0..3]
				random = Math.floor(Math.random() * total)
				total = total - 1
				chosen = phonemesCopy.splice(random, 1)
				buttonMap['button'+i].push chosen[0]
		return buttonMap

window.secKeyboard = new SecKeyboard()

window.privateKey = ""

createPostSignatureXHR = ->
	xhr = new XMLHttpRequest();
	xhr.open('POST', '/signature', true);
	xhr.onload = ->
		hideAllAlerts()
		if this.response is "OK" and this.status is 200
			console.log 'success'
			window.location.href = "/"
		else if (this.status is 403)
				$(".alert-error.username-blocked").fadeIn()
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
		password = $('#pkey-password').val()
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
		hideAllAlerts()
		$(".alert-success.username").fadeIn()
	usernamePromise.fail (jqXHR) ->
		hideAllAlerts()
		console.log jqXHR.status
		if (jqXHR.status is 403)
			$(".alert-error.username-blocked").fadeIn()
		else
			$(".alert-error.username").fadeIn()

	return false

passwordFormHandler = ->
	if window.secKeyboard.chosenPhonemes.length isnt 3
		hideAllAlerts()
		$('.alert-error.password-length').fadeIn()
		return false

	$('.alert-error.password-length').fadeOut()

	promise = $.post('/password', phonemes: window.secKeyboard.chosenPhonemes)
	promise.done (data) ->
		console.log 'Password ok!'
		hideAllAlerts()
		$('.alert-success.password').fadeIn()

	promise.fail (jqXHR) ->
		console.log 'Password failed!', jqXHR.status
		hideAllAlerts()
		if (jqXHR.status is 403)
			$(".alert-error.username-blocked").fadeIn()
		else
			$('.alert-error.password').fadeIn()

	return false

hideAllAlerts = ->
	$('.alert-success.signature').fadeOut()
	$('.alert-error.signature').fadeOut()
	$('.alert-success.password').fadeOut()
	$('.alert-error.password').fadeOut()
	$('.alert-error.password-length').fadeOut()
	$(".alert-success.username").fadeOut()
	$(".alert-error.username").fadeOut()
	$(".alert-error.username-blocked").fadeOut()

$('#loginForm').submit usernameFormHandler
$('#passwordForm').submit passwordFormHandler
$('#checkSignature').submit checkDigitalSignature
$('#file').change changeHandler