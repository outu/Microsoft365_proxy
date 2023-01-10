$password = ConvertTo-SecureString "backup@1234567890" -AsPlainText -Force
$UserCredential = New-Object System.Management.Automation.PSCredential ("Administrator@exch.com.cn", $password)
#$UserCredential = Get-Credential
$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://WIN-TT7P7PN7QHJ.exch.com.cn/PowerShell/ -Authentication Kerberos -Credential $UserCredential
$output = Import-PSSession $Session -DisableNameChecking
Get-Mailbox -ResultSize Unlimited |select displayname,PrimarySmtpAddress
