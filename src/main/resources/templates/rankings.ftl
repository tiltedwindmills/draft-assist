<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div class="container-fluid">

	<p>Drafted so Far: ${positionCount!0}</p>

	<div>
	<table class="table table-condensed">
		<thead>
		<tr>
			<th>Rank</th>
			<#if overall??>
				<th>Round</th>
			</#if>
			<#list rankings as ranking>
				<#if ranking??>
					<th>${ranking.name}
				</#if>
			</#list>
		</tr>
		</thead>
		<#if startRecord < 0>
			<#assign startRecord = 0>
		</#if>

		<tbody>
		<#list startRecord..recordCount-1 as recordNumber>
		<#if (((positionCount)!0)/2)?floor == recordNumber+1><#assign rowBorderClass="border_bottom">
		<#elseif (((positionCount)!0)/2)?floor == recordNumber+2><#assign rowBorderClass="border_bottom">
		<#else><#assign rowBorderClass="">
		</#if>
		<tr class="${rowBorderClass}">
			<td>${recordNumber+1}</td>
			<#if overall??>
				<#assign pickNumber = ((recordNumber) % 12) +1 >
				<td>
					${((recordNumber)/12 + 1)?floor}.<#if pickNumber < 10>0</#if>${pickNumber}
				</td>
			</#if>
			<#assign rankingsCount = rankings?size>
			<#list 0..rankingsCount-1 as rankingNumber>
				<#if rankings[rankingNumber]?? && rankings[rankingNumber].rankedPlayers[recordNumber]??>
					<#assign draftedPlayer = rankings[rankingNumber].rankedPlayers[recordNumber]>

					<#if draftedPlayer.timesDrafted == 1><#assign class="danger">
					<#elseif draftedPlayer.doNotDraft><#assign class="info">
					<#else><#assign class="">
					</#if>
					<td class="${class!""}">
						<#if draftedPlayer.player??>
							${draftedPlayer.player.name} ( ${draftedPlayer.player.age} )
						</#if>
					</td>

				<#else><td></td>
				</#if>
			</#list>
		</tr>
		</#list>
		</tbody>

	</table>
	</div>
</div>

<#include "includes/footer.ftl">
</body>
</html>