<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div class="container">

	<div class="row">

			<table class="table table-condensed">
				<thead>
				<tr>
					<th></th>
					<th>Name</th>
					<th>Age</th>
					<th>Position</th>
					<th>Team</th>
					<th>Points</th>
					<th>Tackles</th>
					<th>Assists</th>
					<th>Sacks</th>
					<th>FFumbles</th>
					<th>FumblRecov</th>
					<th>Int.</th>
					<th>PassD</th>
					<th>TD</th>
				</tr>
				</thead>
				<#list projections as projection>
					<#if projection??>

						<#if projection.player.timesDrafted == 2><#assign class="danger">
						<#elseif projection.player.doNotDraft><#assign class="info">
						<#elseif projection.player.timesDrafted == 1><#assign class="warning">
						<#else><#assign class="">
						</#if>

						<tr class="${class!""}">
							<td>${projection_index + 1}</td>
							<td>${projection.player.player.name}</td>
							<td>${projection.player.player.age}</td>
							<td>${projection.player.player.team}</td>
							<td>${projection.player.player.position}</td>
							<td>${projection.points}</td>
							<td>${projection.tackles}</td>
							<td>${projection.assists}</td>
							<td>${projection.sacks}</td>
							<td>${projection.forcedFumbles}</td>
							<td>${projection.fumblesRecovered}</td>
							<td>${projection.interceptions}</td>
							<td>${projection.passesDefensed}</td>
							<td>${projection.touchdowns}</td>
						</tr>
					</#if>
				</#list>
			</table>

	</div> <!-- end row -->

</div> <!-- end container -->

<#include "includes/footer.ftl">
</body>
</html>