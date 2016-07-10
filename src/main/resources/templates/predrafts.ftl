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
					<th>Team</th>
					<th>Picks</th>
					<th>Pre-drafts</th>
					<th>Average Time</th>
					<th>Average Time</th>
				</tr>
				</thead>
				<#list franchiseStats as franchiseStat>
					<#if franchiseStat??>
						<tr>
							<td>${franchiseStat.franchiseName}</td>
							<td>${franchiseStat.picksCount}</td>
							<td>${franchiseStat.predrafts}</td>
							<td>${franchiseStat.averageTime}</td>
							<td>${franchiseStat.standardDeviation}</td>
						</tr>
					</#if>
				</#list>
			</table>

</div> <!-- end container -->

<#include "includes/footer.ftl">
</body>
</html>