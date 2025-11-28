// @ts-check

const config = {
  title: 'Thunder',
  tagline: 'CTAs, Nudges and Behaviour Tags platform',
  url: 'https://your-domain.example',
  baseUrl: '/',
  favicon: 'img/logo.svg',
  organizationName: 'dream11', // GitHub org/user name.
  projectName: 'thunder-oss', // Repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  trailingSlash: false,

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          path: 'docs',
          routeBasePath: '/', // Docs as root
          sidebarPath: require.resolve('./sidebars.js'),
          editCurrentVersion: false,
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],
};

module.exports = config;



