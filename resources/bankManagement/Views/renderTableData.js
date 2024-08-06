// import { jsonToCsv, download } from './jsonToCsv'; 

// export const renderTableData = (json, tableHeaders) => {
//     document.querySelector('table').innerHTML = `
//         <thead>
//             <tr>
//                 ${tableHeaders.map((header) => `<th>${header}</th>`).join("")}
//             </tr>
//         </thead>
//         <tbody>
//             ${json['data'].map((row) => ` 
//                 <tr>
//                     ${tableHeaders.map((column) => `<td>${row[column]}</td>`).join("")}
//                 </tr>
//             `)}
//         </tbody>
//     `
//     document.getElementById('download').addEventListener('click', () => {
//         let csv = jsonToCsv(json, tableHeaders);
//         download(csv);
//     });
// }