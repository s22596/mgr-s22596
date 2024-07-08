function searchFiles(query, searchSystem) {
    const resultsContainer = document.getElementById('results');
    fetch(`/search${searchSystem}?query=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            resultsContainer.innerHTML = data.map(file => {
                if (file.startsWith('folder ')) {
                    const folderName = file.replace('folder ', '');
                    return `<tr><td><a href="/${searchSystem}/${encodeURIComponent(folderName)}">${folderName}</a></td><td></td></tr>`;
                } else if (file.startsWith('file ')) {
                    const fileName = file.replace('file ', '');
                    return `<tr><td>${fileName}</td><td><a href="/downloadFile${searchSystem}?fileName=${encodeURIComponent(fileName)}">Pobierz</a></td></tr>`;
                }
                return '';
            }).join('');
        })
        .catch(error => console.error('Error:', error));
}
