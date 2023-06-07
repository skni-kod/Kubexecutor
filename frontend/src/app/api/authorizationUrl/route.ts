export const GET = async () => {
  let endpoint = process.env.AUTHORIZE_ENDPOINT || "";
  return new Response(
    JSON.stringify({
      url: endpoint
    })
  );
};
