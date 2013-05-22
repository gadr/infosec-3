class UserKeyboard
	constructor: (@passwordId, @keyboardId, @clearId) ->
		console.log 'Hey!'
		$(@passwordId).attr('readonly', 'readonly')
		@phonemes = ["BA", "CA", "DA", "FA", "GA", "JA", "BE", "BO", "CE", "CO", "DE", "DO", "FE", "FO", "GE", "GO", "JE", "JO", "MA", "ME", "RA", "RO", "VE", "VO"]
		@chosenPhonemes = []
		@createButtons()
		$(@clearId).click @clearPassword

	clearPassword: (e) =>
		$(@passwordId).val('')
		@chosenPhonemes = []
		@createButtons()

	clickButton: (e) =>
		return false if @chosenPhonemes.length is 3
		val = $(e.target).val()
		@chosenPhonemes.push val
		$(@passwordId).val($(@passwordId).val() + val)
		$(e.target).hide()

	createButtons: =>
		buttons = []
		for phoneme in @phonemes
			buttonDOM = $("<input type='button' class='btn user-key' value='#{phoneme}' id='button#{phoneme}'/>")
			buttonDOM.click @clickButton
			buttons.push buttonDOM
		$(@keyboardId).html(buttons)

window.userKeyboard = new UserKeyboard('#password', '#user-keyboard', '#clear-password')
window.userKeyboardConfirmation = new UserKeyboard('#passwordConfirmation', '#user-keyboard-confirm', '#clear-password-confirm')