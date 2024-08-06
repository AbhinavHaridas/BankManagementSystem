export const renderDepositAndWithdraw = async () => {
    document.getElementById("deposit-and-withdraw").innerHTML = `
    <div class="deposit" id="deposit">
        <h3>Deposit</h3> 
        <br/>
        <h5>Enter account number</h5>
        <input class="account-no-box" id="account-no-d-input-box"></input>
        <br />
        <h5>Enter amount to be deposited</h5>
        <input class="deposit-input-box" id="deposit-input-box"></input>
        <br />
        <br />
        <button class="bt-deposit" id="bt-deposit">Deposit</button>
    </div>
    <br />
    <div class="withdraw" id="withdraw">
        <h3>Withdraw</h3> 
        <br/>
        <h5>Enter account number</h5>
        <input class="account-no-box" id="account-no-w-input-box"></input>
        <br/>
        <h5>Enter amount to be withdrawn</h5>
        <input class="withdraw-input-box" id="withdraw-input-box"></input>
        <br />
        <br />
        <button class="bt-withdraw" id="bt-withdraw">Withdraw</button>
    </div>
`
    return true;
}

export const reverseDepositAndWithdraw = async () => {
    if (document.getElementById("deposit")) document.getElementById("deposit").remove();
    if (document.getElementById("withdraw")) document.getElementById("withdraw").remove();
}

export let depositAndWithdrawBoolean = false;

export const addDepositToDatabase = async () => {
    const depositValue = document.getElementById('deposit-input-box').value;
    const accountNumber = document.getElementById('account-no-d-input-box').value;
    if (depositValue === "" || accountNumber === "") {
        alert("Please enter the fields required for deposit");
        location.reload();
        return;
    }
    try {
        const response = await fetch('http://localhost:7000/api/depositIntoAccount', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                amount: depositValue,
                account_number: accountNumber
            })
        });
        const resJson = await response.json();
        if ('error' in resJson) {
            alert(resJson['error']);
        } else {
            alert(resJson['success']);
        }
        location.reload();
    } catch (e) {
        console.error(e);
        alert(e);
    }
}

export const withdrawFromDatabase = async () => {
    const withdrawValue = document.getElementById('withdraw-input-box').value;
    const accountNumber = document.getElementById('account-no-w-input-box').value;
    if (withdrawValue === "" || accountNumber === "") {
        alert("Please enter the withdraw fields");
        location.reload();
        return;
    }
    try {
        const response = await fetch('http://localhost:7000/api/withdrawFromAccount', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                amount: withdrawValue,
                account_number: accountNumber
            })
        });
        const json = await response.json();

        if ('error' in json) {
            alert(json['error']);
        } else if ('failure' in json) {
            alert(json['failure']);
        } else {
            alert(json['success']);
        }
        location.reload();
    } catch (e) {
        console.error(e);
    }
}