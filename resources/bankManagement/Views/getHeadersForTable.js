export const getHeadersForTable = async () => {
    const table = document.getElementById("which-table").value;
    let headers = [];
    if (table.toLowerCase() === "transactions") {
        headers = ["txn_id", "account_number", "amount", "txn_timestamp"]
    } else if (table.toLowerCase() === "user-accounts") {
        headers = ["account_number", "first_name", "last_name", "username", "phone_number", "nominee_first_name", "nominee_last_name", "account_type", "balance", "account_created_by", "account_creation_timestamp", "account_updation_timestamp"]
    } else if (table.toLowerCase() === "account-type") {
        document.getElementById('search-field-value').value = '';
        headers = ["id", "account_type", "account_type_timestamp"]
    }
    return headers;
}