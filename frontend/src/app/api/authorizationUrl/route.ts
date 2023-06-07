import {NextResponse} from "next/server";

export const GET = async () => {
  let endpoint = process.env.AUTHORIZE_ENDPOINT || "";
  console.log(endpoint);
  return new NextResponse(
    JSON.stringify({
      url: endpoint
    })
  );
};
