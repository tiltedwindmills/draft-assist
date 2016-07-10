<!DOCTYPE html>
<html lang="en">
<#include "includes/header.ftl">
<body>

<#include "includes/menu.ftl">
<div class="container">

	Date: ${time?date}
	<br>
	Time: ${time?time}
	<br>
	<br>
	<br>

	<#assign positions = ["QUARTERBACK", "RUNNING_BACK", "WIDE_RECEIVER", "TIGHT_END", "DEFENSIVE_LINE", "LINEBACKER"]>
	<#list tiers as tier>

		<!-- for each tier -->
		<#if tier??>

			<div class="row">

				<div class="col-sm-12">
					<div class="panel panel-default">
						<div class="panel-heading">Tier ${tier_index + 1}</div>
						<div class="panel-body">
							<#list positions as position>

								<div class="col-sm-2">
									<ul>
									<#list tier.draftedPlayers as draftedPlayer>
										<#if draftedPlayer?? && draftedPlayer.player.position == position>
											<#if draftedPlayer.timesDrafted == 0>
												<#assign class="list-group-item-success">
											<#elseif draftedPlayer.timesDrafted == 1>
												<#assign class="list-group-item-warning">
											<#elseif draftedPlayer.timesDrafted == 2>
												<#assign class="list-group-item-danger">
											</#if>

											<li class="list-group-item ${class}">${draftedPlayer.player}</li>
										</#if>
									</#list>
									</ul>
								</div> <!-- end column -->

							</#list>
						</div>
					</div>
				</div>
			</div> <!-- end row -->

		</#if>
	</#list>

</div> <!-- end container -->

<#include "includes/footer.ftl">
</body>
</html>