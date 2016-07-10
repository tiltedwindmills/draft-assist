<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div class="container">

			<#assign tieredPlayerIndex = 0 >
			<table class="table table-condensed">
				<thead>
				<tr>
					<th>#</th>
					<th>Pick</th>
					<th>Player</th>
				</tr>
				</thead>
				<#list onePicks as pick>
					<#if pick??>

						<tr>
							<td>${pick_index}</td>
							<td>${pick.picks[0].round}.${pick.picks[0].pick}</td>
							<td>${pick.player}</td>
						</tr>
					</#if>
				</#list>
			</table>

</div> <!-- end container -->

<#include "includes/footer.ftl">
</body>
</html>