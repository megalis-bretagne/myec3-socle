/**
* Simple adaptation of
* https://github.com/ioko-tapestry-commons/ioko-tapestry-commons/blob/master/tapestry-commons/tapestry-mixins/src/main/resources/uk/co/ioko/tapestry/mixins/mixins/boundCheckbox.js
*/
var boundCheckBox = Class.create();

boundCheckBox.BoundCheckboxMaster = Class.create({
    initialize: function(clientId) {
            this.childArray = new Array();
            this.element = $(clientId);
            this.element.observe("click", this.onClick.bindAsEventListener(this));
            this.element.__boundcheckbox=this;
    },

    registerChild : function(child) {
    	this.childArray.push(child);
    },

    onClick : function(event) {
        this.childArray.forEach(this.changeCheckBoxes(this.element.checked));
        return false;
    },

    changeCheckBoxes : function(selected) {
        return function(child) {
            child.element.checked = selected;
        }
    }

});

boundCheckBox.BoundCheckboxChild = Class.create({
    initialize: function(clientId, masterId) {
		this.childArray = new Array();
        this.element = $(clientId);
        this.element.observe("click", this.onClick.bindAsEventListener(this));

        this.master = $(masterId);
        this.master.__boundcheckbox.registerChild(this);
    },

    onClick : function(event) {
        if ( this.master.checked && !this.element.checked ) {
        	this.master.checked=false;
        }
        return false;
    }

});

Tapestry.Initializer.boundCheckboxLoad = function(spec) {
	if ( spec.masterId != "" ) {
		new boundCheckBox.BoundCheckboxChild(spec.clientId, spec.masterId);
	} else {
		new boundCheckBox.BoundCheckboxMaster(spec.clientId);
	}
};
