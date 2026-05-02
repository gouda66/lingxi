var web_prefix = '/backend'

// 全局时间格式化函数
function formatTime(timeStr) {
  if (!timeStr) return '';
  
  // 处理数组格式 [年, 月, 日, 时, 分, 秒]
  if (Array.isArray(timeStr)) {
    const [year, month, day, hours, minutes, seconds] = timeStr;
    const formattedMonth = String(month).padStart(2, '0');
    const formattedDay = String(day).padStart(2, '0');
    const formattedHours = String(hours || 0).padStart(2, '0');
    const formattedMinutes = String(minutes || 0).padStart(2, '0');
    const formattedSeconds = String(seconds || 0).padStart(2, '0');
    return `${year}-${formattedMonth}-${formattedDay} ${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
  }
  
  // 处理字符串或Date对象
  const date = new Date(timeStr);
  if (isNaN(date.getTime())) return '';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}