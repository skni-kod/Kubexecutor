import { cookies } from "next/headers";

export const GET = async () => {
  const response = await fetch("http://localhost:9000/oauth", {
    credentials: "include",
    redirect: "manual",
  });

  console.log(response.headers);

  cookies().set({
    name: "GoogleCsrf",
    value: response.headers.get("csrf-token"),
  });

  // to tu nie działa
  cookies().set({
    name: "GoogleOriginalUri",
    value: response.headers.get("original-uri"),
  });

  return new Response(
    JSON.stringify({
      url: response.headers.get("location"),
    })
  );
};
