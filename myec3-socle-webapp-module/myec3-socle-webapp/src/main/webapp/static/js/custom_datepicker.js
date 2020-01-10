if (typeof(DatePicker) != 'undefined') {

      DatePicker.prototype.create = function(doc){
             if (doc == null) doc = document;

                this._document = doc;

            	// create elements
                this._el = doc.createElement("div");
                this._el.className = "datePicker";

            	// header
                var div = doc.createElement("div");
                div.className = "header";
                this._el.appendChild(div);

                var headerTable = doc.createElement("table");
                headerTable.className = "headerTable";
                headerTable.cellSpacing = 0;
                div.appendChild(headerTable);

                var tBody = doc.createElement("tbody");
                headerTable.appendChild(tBody);

                var tr = doc.createElement("tr");
                tBody.appendChild(tr);

                var td = doc.createElement("td");
                this._previousMonth = doc.createElement("button");
                this._previousMonth.className = "previousButton";
                this._previousMonth.setAttribute("type", "button");
                td.appendChild(this._previousMonth);
                tr.appendChild(td);

                td = doc.createElement("td");
                td.className = "labelContainer";
                tr.appendChild(td);

                this._topLabel = doc.createElement("a");
                this._topLabel.className = "topLabel";
                this._topLabel.href = "#";
                this._topLabel.appendChild(doc.createTextNode(String.fromCharCode(160)));
                td.appendChild(this._topLabel);

                this._labelPopup = doc.createElement("div");
                this._labelPopup.className = "labelPopup";
            	// no insertion

                td = doc.createElement("td");
                this._nextMonth = doc.createElement("button");
                this._nextMonth.className = "nextButton";
                this._nextMonth.setAttribute("type", "button");
                td.appendChild(this._nextMonth);
                tr.appendChild(td);

            	// grid
                div = doc.createElement("div");
                div.className = "grid";
                this._el.appendChild(div);
                this._table = div;

            	// footer
                div = doc.createElement("div");
                div.className = "footer";
                this._el.appendChild(div);

                var footerTable = doc.createElement("table");
                footerTable.className = "footerTable";
                footerTable.cellSpacing = 0;
                div.appendChild(footerTable);

                tBody = doc.createElement("tbody");
                footerTable.appendChild(tBody);

                tr = doc.createElement("tr");
                tBody.appendChild(tr);

                td = doc.createElement("td");
                this._todayButton = doc.createElement("button");
                this._todayButton.className = "todayButtonCustom";
                this._todayButton.setAttribute("type", "button");
                this._todayButton.appendChild(doc.createTextNode("Ajd'hui"));
                td.appendChild(this._todayButton);
                tr.appendChild(td);

                td = doc.createElement("td");
                td.className = "filler";
                td.appendChild(doc.createTextNode(String.fromCharCode(160)));
                tr.appendChild(td);

                td = doc.createElement("td");
                this._noneButton = doc.createElement("button");
                this._noneButton.className = "noneButtonCustom";
                this._noneButton.setAttribute("type", "button");
                this._noneButton.appendChild(doc.createTextNode("Aucune"));
                td.appendChild(this._noneButton);
                tr.appendChild(td);


                this._createTable(doc);

                this._updateTable();
                this._setTopLabel();

                if (!this._showNone)
                    this._noneButton.style.visibility = "hidden";
                if (!this._showToday)
                    this._todayButton.style.visibility = "hidden";

            	// IE55+ extension
                this._previousMonth.hideFocus = true;
                this._nextMonth.hideFocus = true;
                this._todayButton.hideFocus = true;
                this._noneButton.hideFocus = true;
            	// end IE55+ extension

                // hook up events
                var dp = this;
            	// buttons
                this._previousMonth.onclick = function ()
                {
                    dp.goToPreviousMonth();
                };
                this._nextMonth.onclick = function ()
                {
                    dp.goToNextMonth();
                };
                this._todayButton.onclick = function ()
                {
                    dp.goToToday();
                };
                this._noneButton.onclick = function ()
                {
                    //this should always clear the date and trigger onselected...
                    dp.setDate(null, true);
                };

                this._el.onselectstart = function ()
                {
                    return false;
                };

                this._table.onclick = function (e)
                {
                    // find event
                    if (e == null) e = doc.parentWindow.event;

            		// find td
                    var el = e.target != null ? e.target : e.srcElement;
                    while (el.nodeType != 1)
                        el = el.parentNode;
                    while (el != null && el.tagName && el.tagName.toLowerCase() != "td")
                        el = el.parentNode;

            		// if no td found, return
                    if (el == null || el.tagName == null || el.tagName.toLowerCase() != "td")
                        return;

                    var d = new Date(dp._calendarDate);
                    var n = Number(el.firstChild.data);
                    if (isNaN(n) || n <= 0 || n == null)
                        return;

                    d.setDate(n);
                    dp.setDate(d);
                };

            	// show popup
                this._topLabel.onclick = function (e)
                {
                    dp._showLabelPopup();
                    return false;
                };

                this._el.onkeydown = function (e)
                {
                    if (e == null) e = doc.parentWindow.event;
                    var kc = e.keyCode != null ? e.keyCode : e.charCode;

                    if (kc < 37 || kc > 40) return true;

                    var d = new Date(dp._calendarDate).valueOf();
                    if (kc == 37) // left
                        d -= 24 * 60 * 60 * 1000;
                    else if (kc == 39) // right
                        d += 24 * 60 * 60 * 1000;
                    else if (kc == 38) // up
                        d -= 7 * 24 * 60 * 60 * 1000;
                    else if (kc == 40) // down
                        d += 7 * 24 * 60 * 60 * 1000;

                    dp.setCalendarDate(new Date(d));
                    return false;
                }

            	// ie6 extension
                this._el.onmousewheel = function (e)
                {
                    if (e == null) e = doc.parentWindow.event;
                    var n = - e.wheelDelta / 120;
                    var d = new Date(dp._calendarDate);
                    var m = d.getMonth() + n;
                    d.setMonth(m);


                    dp.setCalendarDate(d);

                    return false;
                }

                doc.onclick  =  function (e) {
                    var targ;

                     // find event
                    if (e == null) e = doc.parentWindow.event;

                    if (e.target) targ = e.target;
                    else if (e.srcElement) targ = e.srcElement;
                    // find classname 'datePicker' as parent
                    var insideDatePicker = null;
                    var parent = targ.parentNode;
                    while (parent != null) {
                        if (parent.className == 'datePicker' || parent.className == 'labelPopup') {
                            insideDatePicker = parent;
                            break;
                        }
                        parent = parent.parentNode;
                    }

                    if (Tapestry.DateField.activeDateField !=  null) {

                        if (insideDatePicker == null && targ.className != 't-calendar-trigger') {
                            Tapestry.DateField.activeDateField.hidePopup();
                            Tapestry.DateField.activeDateField = null;
                        }
                    }
                }
                return this._el;
      };
      
      Tapestry.DateField.prototype.triggerClicked = function(){
            if (this.field.disabled) 
                  return;
            
            if (this.popup == null) {
                  this.createPopup();
                  
            }
            else {
                  if (this.popup.visible()) {
                        this.hidePopup();
                        return;
                  }
            }
            
            
            var value = $F(this.field);
            
            if (value == "") {
                  this.datePicker.setDate(null);
                  
                  this.positionPopup();
                  
                  this.revealPopup();
                  
                  return;
            }
            
            var resultHandler = function(result){
                  var date = new Date();
                  
                  date.setTime(result);
                  
                  this.datePicker.setDate(date);
                  
                  this.positionPopup();
                  
                  this.revealPopup();
            };
            
            var errorHandler = function(message){
                  var date = new Date();
                  
                  this.datePicker.setDate(date);
                  
                  this.positionPopup();
                  
                  this.revealPopup();
                  
                  var currDay = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
                  var currMonth = date.getMonth() < 9 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1);
                  var currYear = date.getFullYear();
                  
                  $(this.field).value = currDay + "/" + currMonth + "/" + currYear;
            };
            
            this.sendServerRequest(this.parseURL, value, resultHandler, errorHandler);
      };
}
