export const dynamic = "force-dynamic"

export async function GET() {
  let endpoint = process.env.AUTHORIZE_ENDPOINT || "";
  return new Response(
    JSON.stringify({
      url: endpoint
    })
  );
};
