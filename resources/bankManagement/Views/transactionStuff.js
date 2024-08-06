// add new row and edit config details should not visible for transaction history table
export const disableStuffForTransactions = () => {
    document.getElementById('add-new-row').style.visibility = 'collapse';
    document.getElementById('config-details').style.visibility = 'collapse';
    return true;
}

// reverting changes made by disableStuffForTransactions as the code is no longer in transaction history table 
export const enableStuff = () => {
    document.getElementById('add-new-row').style.visibility = 'visible';
    document.getElementById('config-details').style.visibility = 'visible';
    return false;
}

// variable that checks if the stuff is disabled or not
export let isDisabled = false;