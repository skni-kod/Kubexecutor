export const dynamic = "force-dynamic"

export async function GET() {
  let endpoint = process.env.AUTHENTICATE_ENDPOINT || "";
  return new Response(
    JSON.stringify({
      url: endpoint
    })
  );
};
