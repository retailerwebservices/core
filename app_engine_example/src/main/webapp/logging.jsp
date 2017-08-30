<html>
<head>
<title>Logging Example</title>
</head>
<body>
	<div>
		See the <a
			href="https://digitalpanda.atlassian.net/wiki/spaces/SD/pages/6717457/Logging">logging</a>
		Confluence page as well.
	</div>
	<h3>SEVERE</h3>
	<form method="post">
		<input type="text" name="SEVERE" id="SEVERE" value="severe message">
		<input type="submit" value="submit">
	</form>
	<h3>WARNING</h3>
	<form method="post">
		<input type="text" name="WARNING" id="WARNING" value="warning message">
		<input type="submit" value="submit">
	</form>
	<h3>INFO</h3>
	<form method="post">
		<input type="text" name="INFO" id="INFO" value="info message">
		<input type="submit" value="submit">
	</form>
	<h3>CONFIG</h3>
	<form method="post">
		<input type="text" name="CONFIG" id="CONFIG" value="config message">
		<input type="submit" value="submit">
	</form>
	<h3>FINE</h3>
	<form method="post">
		<input type="text" name="FINE" id="FINE" value="fine message">
		<input type="submit" value="submit">
	</form>
	<h3>FINER</h3>
	<form method="post">
		<input type="text" name="FINER" id="FINER" value="finer message">
		<input type="submit" value="submit">
	</form>
	<h3>FINEST</h3>
	<form method="post">
		<input type="text" name="FINEST" id="FINEST" value="finest message">
		<input type="submit" value="submit">
	</form>


	<h3>Log an exception</h3>
	<form method="post">
		<input type="text" name="throwme" id="throwmeId"
			value="a severe exception will be thrown"> <input
			type="submit" value="submit">
	</form>

	<div>
		<p>${logUrl}</p>
	</div>

</body>
</html>