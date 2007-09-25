/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/edit1.js,v 1.2 2007-09-25 14:26:46 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    function loaded()
    {
        editForm.save1.disabled = false;
        editForm.save2.disabled = false;
    }

    function setFreq(value)
    {
        if (value == "D")
        {
            editForm.freqWeekly.disabled = true;
            editForm.freqMonthly.disabled = true;
        }
        else if (value == "W")
        {
            editForm.freqWeekly.disabled = false;
            editForm.freqMonthly.disabled = true;
        }
        else
        {
            editForm.freqWeekly.disabled = true;
            editForm.freqMonthly.disabled = false;
        }
    }

    function cmdHelp()
    {
        window.open("/cadsrsentinel/html/help.html", "_blank");
    }

    function cmdSave()
    {
        beginDate = Date.parse(editForm.propBeginDate.value);
        endDate = Date.parse(editForm.propEndDate.value);

		if (editForm.actVerNum.value.search(/[^0-9.]/g) > -1)
		{
            selectTab1(tabMain4);
            editForm.actVerNum.focus();
            alert("A Version number can only contain digits and a period.");
		}
        else if (editForm.propName.value === "")
        {
            selectTab1(tabMain1);
            editForm.propName.focus();
            alert("A Sentinel Name must be provided.");
        }
        else if (editForm.propStatus[3].checked === true && MstatusReason === false)
        {
            selectTab1(tabMain1);
            editForm.propStatusReason.value = "(please explain why this is inactive)";
            editForm.propStatusReason.focus();
            alert("A reason must be given when the Status is set to Inactive.");
        }
        else if (editForm.propStatus[2].checked === true && editForm.propBeginDate.value === "" &&
            editForm.propEndDate.value === "")
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("A begin and/or end date must be given when the Status is set to Active with Dates.");
        }
        else if (editForm.propBeginDate.value !== "" && isNaN(beginDate) === true)
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("The begin date is incorrect.");
        }
        else if (editForm.propEndDate.value !== "" && isNaN(endDate) === true)
        {
            selectTab1(tabMain1);
            editForm.propEndDate.focus();
            alert("The end date is incorrect.");
        }
        else if (isNaN(beginDate) === false && isNaN(endDate) === false && endDate < beginDate)
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("The begin date must preceed the end date.");
        }
        else if (editForm.infoForms.options[0].selected === false &&
            (editForm.infoSchemes.options[0].selected === false || editForm.infoSchemeItems.options[0].selected === false))
        {
            selectTab1(tabMain3);
            window.alert("Classification Schemes/Items and Forms can not both be selected for criteria.  Please change one or both to (All).");
        }
        else // We think the form is good to go.
        {
            for (ndx = 0; ndx < editForm.propRecipients.options.length; ++ndx)
            {
                editForm.propRecipients.options[ndx].selected = true;
            }
            editForm.nextScreen.value = "save";
            editForm.save1.disabled = true;
            editForm.save2.disabled = true;
            editForm.submit();
        }
    }

    function cmdClear()
    {
        editForm.submit();
    }

    function cmdRun()
    {
        editForm.nextScreen.value = "run";
        editForm.submit();
    }

    function cmdBack()
    {
        editForm.nextScreen.value = "back";
        editForm.submit();
    }
    
    function cmdLogout()
    {
        editForm.action = "/cadsrsentinel/do/logout";
        editForm.submit();
    }

    function checkReason(val)
    {
        MstatusReason = (val !== null && val !== "");
    }

    function highlightReason()
    {
        editForm.propStatusReason.select();
    }
    