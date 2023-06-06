import CodeEditor from "@/components/CodeEditor";
import { cookies } from 'next/headers';


export default function Home() {
  const tokenValue = cookies().get("securityServerAuth");

  return (
    <div>
      <CodeEditor authToken={tokenValue?.value ? tokenValue.value.substring(1,tokenValue.value.length-1) : "NO_TOKEN"}/>
    </div>
  )
}
