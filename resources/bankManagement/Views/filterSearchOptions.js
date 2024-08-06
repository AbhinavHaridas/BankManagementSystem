export const filterSearchOptions = (headers) => {
    const excludeHeaders = ['account_creation_timestamp', 'account_updation_timestamp', 'txn_timestamp', 'account_type_timestamp']
    const filteredHeaders = headers.filter((header) => !excludeHeaders.includes(header));

    document.getElementById('search-fields').innerHTML = `
        ${filteredHeaders.map((head) => `
            <option value="${head}">${head}</option>
        `).join('')}
    `;
}