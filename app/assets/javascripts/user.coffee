class UserKeyboard
	constructor: ->
		console.log 'Hey!'
		@phonemes = ["BA", "CA", "DA", "FA", "GA", "JA", "BE", "BO", "CE", "CO", "DE", "DO", "FE", "FO", "GE", "GO", "JE", "JO", "MA", "ME", "RA", "RO", "VE", "VO"]
		@chosenPhonemes = []
		@createButtons()
		$('#clear-password').click @clearPassword

	clearPassword: (e) =>
		$('#password').val('')
		@chosenPhonemes = []
		@createButtons()

	clickButton: (e) =>
		return false if @chosenPhonemes.length is 3
		val = $(e.target).val()
		@chosenPhonemes.push val
		$('#password').val($('#password').val() + val)
		$(e.target).hide()

	createButtons: =>
		buttons = []
		for phoneme in @phonemes
			buttonDOM = $("<input type='button' class='btn user-key' value='#{phoneme}' id='button#{phoneme}'/>")
			buttonDOM.click @clickButton
			buttons.push buttonDOM
		$("#user-keyboard").html(buttons)

window.userKeyboard = new UserKeyboard()