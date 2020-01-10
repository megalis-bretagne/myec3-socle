function initPage() {
	var loadCheckedSize = jQuery('input:checked[name=selected]').size();

	jQuery("#submit")
			.click(
					function() {
						var answer = true;
						var submitCheckedSize = jQuery(
								'input:checked[name=selected]').size();

						if (loadCheckedSize > submitCheckedSize) {
							answer = confirm('Êtes-vous sûr(e) de vouloir continuer ? Retirer un abonnement à un service supprimera les rôles des utilisateurs sur ce service de manière irréversible.');
						}
						return answer;
					});
};
