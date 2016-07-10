<!DOCTYPE html>
<html lang="en">
<head>
	<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
	<link href="http://fonts.googleapis.com/css?family=Quicksand:300,400" rel="stylesheet" type="text/css">
	<link href="css/styles.css" rel="stylesheet" type="text/css"
</head>
<body>

	<ul>
	<#list players as player>
		<#if player??>
			<li class="list-group-item">${player.name} ${player.team} ${player.position}</li>
		</#if>
	</#list>
	</ul>


<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>