export const getLatestTransaction = () => {
    const latestTransactionDiv = document.getElementById('get-latest-transaction');
    latestTransactionDiv.innerHTML = `
        <div class="latestTransaction">
            <h3>Get Latest Transaction</h3>
            <h5>Enter the account number that you want</h5>
            <input class="latest-transaction-search-box" id="latest-transaction-search-box"></input>
            <br />
            <br />
            <button class="latest-transaction-bt" id="latest-transaction-bt">Get it</button> 
            <br />
            <h5>Transaction timestamp : <p id="latest-transaction-timestamp"></p></h5>
            <h5>Transaction amount : <p id="latest-transaction-amount"></p></h5>
            <h5>Transaction type : <p id="latest-transaction-type"></p></h5>
        </div>
    `;
    return true;
}

export let isTransaction = false;

export const revertLatestTransaction = () => {
    const latestTransactionDiv = document.getElementById('get-latest-transaction');
    latestTransactionDiv.innerHTML = ``;
    return false;
}

export const fetchLatestTransaction = async () => {
    const accountNumber = document.getElementById("latest-transaction-search-box").value;
    try {
        const response = await fetch('http://localhost:7000/api/getLatestTransaction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                account_number: accountNumber
            })
        });
        const responseJson = await response.json();
        if ('error' in responseJson) {
            alert(responseJson['error']);
        } else {
            document.getElementById('latest-transaction-timestamp').innerText = responseJson['data'][0]['txn_timestamp'];
            document.getElementById('latest-transaction-amount').innerText = "Rs " + responseJson['data'][0]['amount'];
            document.getElementById('latest-transaction-type').innerText = responseJson['data'][0]['transaction_type'];
        }
    } catch (e) {
        console.error(e);
        alert(e);
    } 
}