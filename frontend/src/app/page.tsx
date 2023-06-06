import CodeEditor from "@/components/CodeEditor";
import { cookies } from 'next/headers';


export default function Home() {
  const tokenCookie = cookies().get("securityServerAuth");
  const tokenValue = tokenCookie?.value ? tokenCookie.value.substring(1,tokenCookie.value.length-1) : null
  return (
    <div>
      <CodeEditor authToken={tokenValue}/>
    </div>
  )
}
