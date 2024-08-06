export const renderBalanceOfAnAccount = () => {
    document.getElementById('balance-of-an-account').style.visibility = 'visible';
    document.getElementById('balance-of-an-account').innerHTML = `
        <h3>Balance of an account</h3>
        <h5>Enter the account whose balance you wish to see</h5>
        <input type="text" id="account-number-balance-input"></input>
        <button id="account-number-balance-button">Get balance</button>
        <h5>The balance is : <h5 id="account-number-balance-output"></h5></h5>
    `
}

export const revertBalanceOfAnAccount = () => {
    document.getElementById('balance-of-an-account').style.visibility = 'collapse';
    document.getElementById('balance-of-an-account').innerHTML = ``;
}

export const fetchBalanceOfAnAccount = async () => {
    const value = document.getElementById('account-number-balance-input').value; 
    try {
        const response = await fetch('http://localhost:7000/api/getBalance', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                account_number: value
            })
        });

        const responseJson = await response.json();
        if ('error' in responseJson) {
            alert(responseJson['error']);
        } else {
            document.getElementById('account-number-balance-output').innerText = "Rs " + responseJson['data']['balance'];
        }
    } catch(e) {
        alert(e);
    }
}