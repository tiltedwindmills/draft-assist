<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div class="container-fluid">

	<div class="row">
		<div class="col-md-12">

			<#assign tieredPlayerIndex = 0 >
			<table class="table table-condensed">
				<thead>
				<tr>
					<th>Pick</th>
					<th>Picks Away</th>
					<th>Tier</th>
					<th>Player</th>
				</tr>
				</thead>

				<#assign picksAway = 0>
				<#list picks as pick>
					<#if pick?? && pick.playerId == "">
						<#assign picksAway = picksAway + 1>

						<#if pick.franchise == myfranchise><#assign myPickClass="success">
						<#else><#assign myPickClass="">
						</#if>

						<tr class="${myPickClass!""}">
							<td>${pick.round}.${pick.pick}</td>
							<td>${picksAway}</td>

							<#if tieredPlayers?? &&
								tieredPlayers[tieredPlayerIndex]?? &&
								tieredPlayers[tieredPlayerIndex].player?? &&
								tieredPlayerIndex < tieredPlayers?size>

								<#assign tierNumber = tieredPlayers[tieredPlayerIndex].tierNumber + 1>
								<td>

									<#if tierNumber == 1><#assign tierClass="label-success">
									<#elseif tierNumber == 2><#assign tierClass="label-primary">
									<#elseif tierNumber == 3><#assign tierClass="label-info">
									<#elseif tierNumber == 4><#assign tierClass="label-warning">
									<#else><#assign tierClass="label-default">
									</#if>

									<span class="label ${tierClass}">Tier ${tierNumber}</span>
								</td>

								<td>${tieredPlayers[tieredPlayerIndex].player}</td>

								<#assign tieredPlayerIndex = tieredPlayerIndex + 1 >
							<#else>
								<td colspan="2"></td>
							</#if>
						</tr>
					</#if>
				</#list>
			</table>
		</div> <!-- end col-md-8 -->

		<div class="col-md-4">
			<p>Picks completed: ${draftedPlayerCount}</p>
			<p>Average Time Per Pick: ${averageTimePerPick / 1000 / 60 / 60}</p>
			<p>Estimated Completion: ${estimatedEnd?date} - ${estimatedEnd?time}<p>

			<div class="progress">
				<div class="progress-bar" role="progressbar" aria-valuenow="${picks?size}" aria-valuemin="1" aria-valuemax="1080" style="min-width: 2em; width: ${draftedPlayerCount / 1080 * 100}%;">
					${draftedPlayerCount / 1080 * 100}%
				</div>
			</div>

			<ul class="list-group">
				<li class="list-group-item">Quarterback <span class="badge">${(draftedPositionMap["QB"]!0)}</span></li>
				<li class="list-group-item">Running Back <span class="badge">${(draftedPositionMap["RB"]!0)}</span></li>
				<li class="list-group-item">Wide Receiver <span class="badge">${(draftedPositionMap["WR"]!0)}</span></li>
				<li class="list-group-item">Tight End <span class="badge">${(draftedPositionMap["TE"]!0)}</span></li>
				<li class="list-group-item">Kicker <span class="badge">${(draftedPositionMap["PK"]!0)}</span></li>
				<li class="list-group-item">Defensive Line <span class="badge">${((draftedPositionMap["DT"]!0) + (draftedPositionMap["DE"]!0))}</span></li>
				<li class="list-group-item">Linebacker <span class="badge">${(draftedPositionMap["LB"]!0)}</span></li>
				<li class="list-group-item">Defensive Back <span class="badge">${((draftedPositionMap["CB"]!0) + (draftedPositionMap["S"]!0))}</span></li>
			</ul>

		</div> <!-- end col-md-4 -->

	</div> <!-- end row -->

</div> <!-- end container -->

<#include "includes/footer.ftl">
</body>
</html>