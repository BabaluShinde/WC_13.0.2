<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc" %>

    <!-- customParticipantsTable.jsp -->

            <script type="text/javascript">

                function getAllParticipantFields() {
                    var fields = [];
                    var allSelects = document.querySelectorAll('select');
                    for (var i = 0; i < allSelects.length; i++) {
                        var onchangeAttr = allSelects[i].getAttribute('onchange');
                        if (onchangeAttr && onchangeAttr.indexOf('onParticipantChange') !== -1) {
                            fields.push(allSelects[i]);
                        }
                    }
                    return fields;
                }

                //  Validate all roles are selected 
                function validateAllRolesSelected() {
                    var fields = getAllParticipantFields();
                    var missing = [];

                    fields.forEach(function (el) {
                        if (!el.value || el.value.trim() === "") {
                            missing.push(el.id); // collect missing role IDs
                        }
                    });

                    var allSelected = missing.length === 0 && fields.length > 0;

                    //  Update UI banners
                    var banner = document.getElementById("participantsWarning");
                    var successMsg = document.getElementById("participantsSuccess");

                    if (allSelected) {
                        if (banner) banner.style.display = "none";
                        if (successMsg) successMsg.style.display = "block";
                    } else {
                        if (banner) banner.style.display = "block";
                        if (successMsg) successMsg.style.display = "none";
                    }

                    // Banner gives inline feedback — alert only fires on Complete button click

                    return allSelected;
                }

            </script>          

            <!--  Warning Banner -->
            <div id="participantsWarning" style="color:red; font-weight:bold; font-size:16px; margin-bottom:10px;">
                All Reviewer and Approver participants are mandatory.
            </div>

            <!--  Success Message -->
            <div id="participantsSuccess"
                style="color:green; font-weight:bold; font-size:16px; margin-bottom:10px; display:none;">
                All roles have assigned users!
            </div>

            <mvc:attributePanel />

            <script>
                function onParticipantChange(el) {

                    //  selected value
                    var value = el.value;

                    //  role (simple & reliable)
                    var roleId = el.id;

                    // extract from name
                    var name = el.name;

                    // Extract OID from name (e.g., OR:wt.workflow.work.WorkItem:888695)
                    var oidMatch = name.match(/(OR:[^$]+)/);
                    var oid = oidMatch ? oidMatch[1] : "";

                    sendToServer(roleId, value, oid);

                    validateAllRolesSelected();

                    return true;
                }
            </script>

            <script>
                function sendToServer(roleId, value, oid) {
                    if (!oid) {
                        console.warn("Could not extract OID, cannot update backend.");
                        return;
                    }

                    var xhr = new XMLHttpRequest();

                    // URL → your custom JSP
                    var url = "/Windchill/netmarkets/jsp/ext/updateParticipant.jsp"; // adjusted filename

                    xhr.open("POST", url, true);
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                    xhr.onreadystatechange = function () {
                        if (xhr.readyState === 4) {
                            console.log("Server Response:", xhr.responseText);
                        }
                    };

                    // send role + value + oid
                    var params = "roleId=" + encodeURIComponent(roleId) +
                        "&user=" + encodeURIComponent(value) +
                        "&oid=" + encodeURIComponent(oid);

                    xhr.send(params);
                }

            </script>