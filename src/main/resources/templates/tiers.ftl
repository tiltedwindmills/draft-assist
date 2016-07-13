<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div id="tierList" class="container-fluid">


	<#assign positions = ["QUARTERBACK", "RUNNING_BACK", "WIDE_RECEIVER", "TIGHT_END", "DEFENSIVE_END", "LINEBACKER", "SAFETY", "CORNERBACK"]>
	<#list tiers as tier>
		<#if tier?? && tier.draftedPlayers?? && !tier.allDrafted>
			<div class="row">
				<#list positions as position>
					<div class="col-md-2">
					<ul class="list-group">
						<#list tier.draftedPlayers as draftedPlayer>
							<#if draftedPlayer?? && draftedPlayer.player.position == position>

								<#if draftedPlayer.timesDrafted gt 0>
									<#assign class="list-group-item-danger">
								<#else>
									<#assign class="">
								</#if>
								<li class="list-group-item ${class!""}">${draftedPlayer.player.name}  ( ${draftedPlayer.player.age} )}</li>
							</#if>
						</#list>
					</ul>
					</div>
				</#list>
			</div>
		</#if>
	</#list>

	<div>


<#include "includes/footer.ftl">
</body>
</html>